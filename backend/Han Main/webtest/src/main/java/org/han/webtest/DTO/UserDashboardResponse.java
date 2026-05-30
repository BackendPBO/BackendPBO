package org.han.webtest.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDashboardResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
}
