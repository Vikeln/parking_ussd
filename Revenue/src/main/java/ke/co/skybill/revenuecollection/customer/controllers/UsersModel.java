package ke.co.skybill.revenuecollection.customer.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersModel {
    private List<Integer> users;
}
