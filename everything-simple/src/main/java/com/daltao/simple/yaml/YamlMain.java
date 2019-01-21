package com.daltao.simple.yaml;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class YamlMain {
    public static void main(String[] args) throws IOException {
        User user = new User();
        user.setAge(18);
        user.setName("xx");
        Contact contact = new Contact();
        contact.setEmail("123456@123.com");
        contact.setMobile("123456");
        user.setContact(contact);
        user.setRoles(Arrays.asList("a", "b"));

        User user2 = new User();
        user2.setAge(18);
        user2.setName("xx");
        Contact contact2 = new Contact();
        contact2.setEmail("123456@123.com");
        contact2.setMobile("123456");
        user2.setContact(contact2);
        user2.setRoles(Arrays.asList("a", "b"));
        new ObjectMapper(new YAMLFactory()).writer().writeValues(new File("D:/TEMP/temp2.yml")).write(user).write("---");
    }

    @Data
    public static class Contact {
        private String mobile;
        private String email;
    }

    @Data
    public static class User {
        private int age;
        private String name;
        private Contact contact;
        private List<String> roles;
    }
}
