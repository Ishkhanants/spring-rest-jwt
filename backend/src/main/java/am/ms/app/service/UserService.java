package am.ms.app.service;

import am.ms.app.model.dto.UserDTO;
import am.ms.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Mirzoyan
 * @author Seroja Grigoryan
 */

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    public User save(UserDTO userDTO){
        repository.save(userDTO);
        return new User(userDTO.getUsername(), userDTO.getPassword(), new ArrayList<>());
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<UserDTO> list = repository.findAll();

        UserDTO dto = null;

        for (UserDTO userDTO : list) {
            if (userDTO.getUsername().equals(s)) {
                dto = userDTO;
                break;
            }
        }

        if (dto != null){
            return new User(dto.getUsername(), dto.getPassword(), new ArrayList<>());
        }

        throw new RuntimeException("User not found!");
    }
}