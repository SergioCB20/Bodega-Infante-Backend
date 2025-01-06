package com.sergio.bodegainfante.services;

import com.sergio.bodegainfante.dtos.AdminInfoDTO;
import com.sergio.bodegainfante.dtos.CustomerInfoDTO;
import com.sergio.bodegainfante.dtos.UserUpdateDTO;
import com.sergio.bodegainfante.models.User;

public interface IUserService {
    public CustomerInfoDTO getCustomerInfo(String email);
    public AdminInfoDTO getAdminInfo(String email);
    public UserUpdateDTO updateUserInfo(String email, UserUpdateDTO userUpdateDTO);
}
