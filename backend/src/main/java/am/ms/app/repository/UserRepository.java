package am.ms.app.repository;

import am.ms.app.model.dto.UserDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Martin Mirzoyan
 * @author Seroja Grigoryan
 */

@Repository
public interface UserRepository extends JpaRepository<UserDTO, Long> {
}
