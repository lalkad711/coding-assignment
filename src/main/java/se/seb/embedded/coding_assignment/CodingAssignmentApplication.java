/* (C)2024 */
package se.seb.embedded.coding_assignment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info =
                @Info(
                        title = "Payments Intiaition and Aggregation",
                        contact = @Contact(name = "Lalit Kadam", email = "lalkad711@yahoo.com")))
@SpringBootApplication
public class CodingAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodingAssignmentApplication.class, args);
    }
}
