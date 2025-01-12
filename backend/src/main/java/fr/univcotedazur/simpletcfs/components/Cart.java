package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.CartModifier;
import fr.univcotedazur.simpletcfs.CartProcessor;
import fr.univcotedazur.simpletcfs.Payment;
import fr.univcotedazur.simpletcfs.entities.Customer;
import fr.univcotedazur.simpletcfs.entities.Item;
import fr.univcotedazur.simpletcfs.entities.Order;
import fr.univcotedazur.simpletcfs.exceptions.EmptyCartException;
import fr.univcotedazur.simpletcfs.exceptions.NegativeQuantityException;
import fr.univcotedazur.simpletcfs.exceptions.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Transactional // All public methods are wrapped in a transaction with commit at end + rollback in case of exceptions
public class Cart implements CartModifier, CartProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(Cart.class);

    @Autowired
    private Payment cashier;

    @Override
    public int update(Customer c, Item item) throws NegativeQuantityException {
        // some very basic logging (see the AOP way for a more powerful approach, in class ControllerLogger)
        LOG.info("TCFS:Cart-Component: Updating cart of " + c.getName() + " with " + item);
        int newQuantity = item.getQuantity();
        Set<Item> items = contents(c);
        Optional<Item> existing = items.stream().filter(e -> e.getCookie().equals(item.getCookie())).findFirst();
        if (existing.isPresent()) {
            newQuantity += existing.get().getQuantity();
        }
        if (newQuantity >= 0) {
            existing.ifPresent(items::remove);
            if (newQuantity > 0) {
                items.add(new Item(item.getCookie(), newQuantity));
            }
        } else {
            throw new NegativeQuantityException(c.getName(), item.getCookie(), newQuantity);
        }
        c.setCart(items);
        return newQuantity;
    }

    @Override
    public Set<Item> contents(Customer c) {
        // In the persistent version, contents is just a shortcut to c.getCart()
        // It is only kept to ease the differentiation between the in-memory and persistent versions
        return c.getCart();
    }

    @Override
    public double price(Customer c) {
        double result = 0.0;
        for (Item item : contents(c)) {
            result += (item.getQuantity() * item.getCookie().getPrice());
        }
        return result;
    }

    @Override
    public Order validate(Customer c) throws PaymentException, EmptyCartException {
        if (contents(c).isEmpty())
            throw new EmptyCartException(c.getName());
        Order res = cashier.payOrder(c, contents(c));
        contents(c).clear();
        return res;
    }
}
