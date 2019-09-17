package cn.xue.repo.socket;

import cn.xue.model.socket.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findBySrcUserIdAndDesUserId(Long srcUserId, Long desUserId, Pageable pageable);
    List<Message> findByDesUserIdAndAlreadySent(Long desUserId, Boolean alreadySent);
}
