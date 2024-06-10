package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.AdressId;
import com.sb.shippingbackend.entity.Customer;
import com.sb.shippingbackend.repository.AddressRepository;
import com.sb.shippingbackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    public ReqRes addAddress(ReqRes addRequest) {
        ReqRes resp = new ReqRes();
        try {
            Customer customer = customerRepository.findById(addRequest.getCustomerId()).orElse(null);
            if(customer != null) {
                AdressId addressId = new AdressId();
                Address address = new Address();
                addressId.setAddress(addRequest.getAddress());
                addressId.setId(addRequest.getCustomerId());
                address.setAddressId(addressId);
                address.setCustomer(customer);
                Address addressResult = addressRepository.save(address);
                if(addressResult != null && !addressResult.getAddressId().getId().isEmpty()) {
                    resp.setAddressObject(addressResult);
                    resp.setMessage("Successful!");
                    resp.setStatusCode(200);
                }
            }
        }
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes updateAddress(ReqRes updateRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Address> optionalAddress = addressRepository.findByCustomerId(updateRequest.getCustomerId());

            if (optionalAddress.isPresent()) {
                Address address = optionalAddress.get();

                address.getAddressId().setAddress(updateRequest.getAddress());

                Address addressResult = addressRepository.save(address);

                if (addressResult != null) {
                    resp.setAddressObject(addressResult);
                    resp.setMessage("Successful!");
                    resp.setStatusCode(200);
                } else {
                    resp.setMessage("Failed to update address!");
                    resp.setStatusCode(500);
                }
            } else {
                resp.setMessage("Address not found for the given customer!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes updateCustomer(ReqRes updateRequest) {
        ReqRes resp = new ReqRes();
        try {
            String updatedId = updateRequest.getCustomerId();
            if (updatedId != null && !updatedId.isEmpty()) {
                Customer customer = customerRepository.findById(updatedId).orElseThrow();
                customer.setName(updateRequest.getName());
                customer.setPhoneNumber(updateRequest.getPhoneNumber());
                Customer updatedCustomer = customerRepository.save(customer);

                resp.setMessage("Customer updated successfully!");
                resp.setStatusCode(200);
                resp.setName(updatedCustomer.getName());
                resp.setPhoneNumber(updatedCustomer.getPhoneNumber());
            } else {
                resp.setMessage("Customer not found!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes deleteCustomer(ReqRes deleteRequest) {
        ReqRes resp = new ReqRes();
        try {
            String deletedId = deleteRequest.getOrderId();
            if (deletedId != null && !deletedId.isEmpty()) {
               customerRepository.deleteById(deletedId);
                resp.setMessage("Customer deleted successfully!");
                resp.setStatusCode(200);
            } else {
                resp.setMessage("Customer not found!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
