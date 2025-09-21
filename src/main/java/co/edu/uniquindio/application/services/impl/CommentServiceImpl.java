package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.CommentDTO;
import co.edu.uniquindio.application.dto.CreateCommentDTO;
import co.edu.uniquindio.application.services.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final List<CommentDTO> comments = new ArrayList<>();

    @Override
    public void create(CreateCommentDTO dto) throws Exception {
        String id = UUID.randomUUID().toString();
        CommentDTO comment = new CommentDTO(
                id,
                dto.userId(),
                dto.accommodationId(),
                dto.content(),
                dto.rating(),
                LocalDateTime.now() // aquí es donde se setea el createdAt
        );
        comments.add(comment);
    }

    @Override
    public CommentDTO get(String id) throws Exception {
        return comments.stream()
                .filter(c -> c.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new Exception("Comentario no encontrado"));
    }

    @Override
    public void delete(String id) throws Exception {
        boolean removed = comments.removeIf(c -> c.id().equals(id));
        if (!removed) {
            throw new Exception("Comentario no encontrado");
        }
    }

    @Override
    public List<CommentDTO> listByAccommodation(String accommodationId) {
        return comments.stream()
                .filter(c -> c.accommodationId().equals(accommodationId))
                .toList();
    }

    @Override
    public List<CommentDTO> listByUser(String userId) {
        return comments.stream()
                .filter(c -> c.userId().equals(userId))
                .toList();
    }
}
