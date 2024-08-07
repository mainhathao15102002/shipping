package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.AddressReq;
import com.sb.shippingbackend.dto.request.UpdateCustomerReq;
import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.AdressId;
import com.sb.shippingbackend.entity.Customer;
import com.sb.shippingbackend.repository.AddressRepository;
import com.sb.shippingbackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AddressRepository addressRepository;
    public ReqRes addAddress(AddressReq addRequest) {
        ReqRes resp = new ReqRes();
        try {
            Customer customer = customerRepository.findById(addRequest.getCustomerId()).orElse(null);
            if(customer != null) {
                AdressId addressId = new AdressId();
                Address address = new Address();
                addressId.setAddress(addRequest.getAddress());
                addressId.setId(addRequest.getCustomerId());
                address.setAddressId(addressId);
                address.setPhoneNumber(addRequest.getPhoneNumber());
                address.setName(addRequest.getName());
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
    public ReqRes updateAddress(AddressReq updateRequest) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Address> optionalAddress = addressRepository.findByCustomerIdAndAddress(updateRequest.getCustomerId(), updateRequest.getOldAddress());
            if (optionalAddress.isPresent()) {
                Address address = optionalAddress.get();
                if (!updateRequest.getAddress().isEmpty()) {
                    address.getAddressId().setAddress(updateRequest.getAddress());
                }

                if (!updateRequest.getName().isEmpty()) {
                    address.setName(updateRequest.getName());
                }
                if (!updateRequest.getPhoneNumber().isEmpty()) {
                    address.setPhoneNumber(updateRequest.getPhoneNumber());
                }
                addressRepository.updateAddress(address.getCustomer().getId(), updateRequest.getOldAddress(), address.getAddressId().getAddress());
                resp.setMessage("Address updated successfully!");
                resp.setStatusCode(200);
            } else {
                resp.setMessage("Address not found for the given customer and address!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes updateCustomer(UpdateCustomerReq updateRequest) {
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
    public ReqRes findCustomerById(String customerId) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            if (optionalCustomer.isPresent()) {
                resp.setCustomer(optionalCustomer.get());
                resp.setMessage("Customer found successfully!");
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
    public ReqRes deleteAddressByAddress(String customerId, String address) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Address> optionalAddress = addressRepository.findByCustomerIdAndAddress(customerId, address);
            if (optionalAddress.isPresent()) {
                addressRepository.delete(optionalAddress.get());
                resp.setMessage("Address deleted successfully!");
                resp.setStatusCode(200);
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
    public ReqRes getAddressByCustomerId(String customerId, String address) {
        ReqRes resp = new ReqRes();
        try {
            Optional<Address> optionalAddress = addressRepository.findByCustomerIdAndAddress(customerId, address);
            if (optionalAddress.isPresent()) {
                resp.setAddressObject(optionalAddress.get());
                resp.setMessage("Address found successfully!");
                resp.setStatusCode(200);
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
    public ReqRes getAllAddressesByCustomerId(String customerId) {
        ReqRes resp = new ReqRes();
        try {
            List<Address> addresses = addressRepository.findAllByCustomerId(customerId);
            if (addresses.isEmpty()) {
                resp.setMessage("No addresses found for the given customer!");
                resp.setStatusCode(404);
            } else {
                resp.setAddressList(addresses);
                resp.setMessage("Addresses found successfully!");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }}
