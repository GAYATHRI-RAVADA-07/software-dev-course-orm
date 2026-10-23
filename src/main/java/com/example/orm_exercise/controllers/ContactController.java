package com.example.orm_exercise.controllers;

import com.example.orm_exercise.models.Contact;
import com.example.orm_exercise.repositories.ContactRepository;
import org.springframework.web.bind.annotation.*;
import com.example.orm_exercise.models.Address;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());
            return contactRepository.save(contact);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable int id) {
        contactRepository.deleteById(id);
    }


    @PostMapping("/{contactId}/addresses")
    public Contact addAddress(
            @PathVariable int contactId,
            @RequestBody Address address) {

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contact not found"));

        // Connect both sides of the relationship.
        contact.addAddress(address);

        // Cascade saves the new address.
        return contactRepository.save(contact);
    }

    @DeleteMapping("/{contactId}/addresses/{addressId}")
    public Contact deleteAddress(
            @PathVariable int contactId,
            @PathVariable int addressId) {

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = contact.getAddresses().stream()
                .filter(a -> a.getId() == addressId)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Address not found for this contact"));

        contact.removeAddress(address);

        // orphanRemoval deletes the removed address.
        return contactRepository.save(contact);
    }

}
