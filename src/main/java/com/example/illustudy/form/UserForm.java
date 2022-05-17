package com.example.illustudy.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.example.illustudy.validation.constraints.PasswordEquals;

import lombok.Data;


@Data
@PasswordEquals
public class UserForm {

    @NotEmpty
    @Size(max = 100)
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(max = 20)
    private String password;

    @NotEmpty
    @Size(max = 20)
    private String passwordConfirmation;

}