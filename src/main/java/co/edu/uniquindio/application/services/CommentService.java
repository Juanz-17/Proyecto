package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.CommentDTO;
import co.edu.uniquindio.application.dto.CreateCommentDTO;

import java.util.List;

public interface CommentService {

    void create(CreateCommentDTO dto) throws Exception;

    CommentDTO get(String id) throws Exception;

    void delete(String id) throws Exception;

    List<CommentDTO> listByAccommodation(String accommodationId);

    List<CommentDTO> listByUser(String userId);
}
