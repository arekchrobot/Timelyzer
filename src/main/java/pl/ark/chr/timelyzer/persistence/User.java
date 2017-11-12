package pl.ark.chr.timelyzer.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(value = "users", noClassnameStored = true)
public class User {

    @Id
    private String email;
    private String password;
    @Embedded
    private Set<Role> roles;
}
