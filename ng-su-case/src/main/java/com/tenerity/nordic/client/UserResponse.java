package com.tenerity.nordic.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenerity.nordic.dto.EntityResponse;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponse extends EntityResponse {
    private List<UserDto> users;

    public List<UserDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
