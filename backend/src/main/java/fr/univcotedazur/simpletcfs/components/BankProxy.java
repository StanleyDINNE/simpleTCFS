package fr.univcotedazur.simpletcfs.components;

import fr.univcotedazur.simpletcfs.Bank;
import fr.univcotedazur.simpletcfs.components.externaldto.PaymentDTO;
import fr.univcotedazur.simpletcfs.entities.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BankProxy implements Bank {

    @Value("${bank.host.baseurl}")
    private String bankHostandPort;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean pay(Customer customer, double value) {
        int result = restTemplate.postForObject(bankHostandPort+"/mailbox",
                new PaymentDTO(customer.getCreditCard(), value),
                Integer.class);
        return (result>0);
    }

}