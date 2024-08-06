package net.grayfield.spb.hobbylog.domain.user.struct;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    private String username;

    private String profileImage;

    private String email;

    private String vendor;

    private String vendorId;

    private List<Role> roles;
}
