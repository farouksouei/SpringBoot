package ma.enset.backend;

import ma.enset.backend.entities.Role;
import ma.enset.backend.entities.User;
import ma.enset.backend.services.UserService;
import ma.enset.backend.services.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserService userService, RoleService roleService) {
        return args -> {


            /*
            // Create roles
            Role userRole = new Role();
            userRole.setName("USER");
            roleService.saveRole(userRole);

                        Role userRole = new Role();
            userRole.setName("RSSI");
            roleService.saveRole(userRole);

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleService.saveRole(adminRole);

            // Create users
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword("12345");
            user1.setEnabled(true);
            userService.saveUser(user1);
            userService.addRoleToUser(user1.getUsername(), "USER");

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("12345");
            admin.setEnabled(true);
            userService.saveUser(admin);
            userService.addRoleToUser(admin.getUsername(), "ADMIN");
            userService.addRoleToUser(admin.getUsername(), "USER");

            User farouk = new User();
            farouk.setUsername("farouksouei@gmail.com");
            farouk.setPassword("Zla7indaf*");
            farouk.setEnabled(true);
            userService.saveUser(farouk);
            userService.addRoleToUser(farouk.getUsername(), "ADMIN");
            userService.addRoleToUser(farouk.getUsername(), "USER");

             */
        };
    }
}
