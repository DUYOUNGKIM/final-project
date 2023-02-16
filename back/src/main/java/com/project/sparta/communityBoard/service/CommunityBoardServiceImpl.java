package com.project.sparta.communityBoard.service;

import static java.util.Arrays.stream;

import com.project.sparta.common.dto.PageResponseDto;
import com.project.sparta.communityBoard.dto.CommunityBoardResponseDto;
import com.project.sparta.communityBoard.entity.CommunityBoard;
import com.project.sparta.communityBoard.repository.BoardRepository;
import com.project.sparta.communityComment.dto.CommunityRequestDto;
import com.project.sparta.communityComment.dto.CommunityResponseDto;
import com.project.sparta.communityComment.dto.CommunityWithLikeResponseDto;
import com.project.sparta.communityComment.entity.CommunityComment;
import com.project.sparta.communityBoard.dto.CommunityBoardRequestDto;
import com.project.sparta.exception.CustomException;
import com.project.sparta.exception.api.Status;
import com.project.sparta.like.entity.CommentLike;
import com.project.sparta.like.repository.LikeBoardRepository;
import com.project.sparta.like.repository.LikeCommentRepository;
import com.project.sparta.security.UserDetailsImpl;
import com.project.sparta.user.entity.User;
import com.project.sparta.user.entity.UserRoleEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityBoardServiceImpl implements CommunityBoardService {

  private final BoardRepository boardRepository;
  private final LikeBoardRepository likeBoardRepository;
  private final LikeCommentRepository likeCommentRepository;

  @Override
  @Transactional
  public void createCommunityBoard(CommunityBoardRequestDto communityBoardRequestDto,
      User user) {
    CommunityBoard communityBoard = new CommunityBoard(communityBoardRequestDto, user);
    boardRepository.saveAndFlush(communityBoard);
//    CommunityBoardResponseDto communityBoardResponseDto = new CommunityBoardResponseDto().builder()
//        .title(communityBoard.getTitle())
//        .nickName(communityBoard.getNickName())
//        .contents(communityBoard.getContents())
//        .id(communityBoard.getId())
//        .communityComments(new ArrayList<>())
//        .build();
  }

  @Override
  @Transactional
  public void updateCommunityBoard(Long community_board_id, CommunityBoardRequestDto communityBoardRequestDto,
      User user) {
    CommunityBoard communityBoard = boardRepository.findByIdAndUser_NickName(community_board_id,user.getNickName())
        .orElseThrow(() -> new CustomException(Status.NOT_FOUND_COMMUNITY_BOARD));
    communityBoard.updateBoard(communityBoardRequestDto);
    boardRepository.saveAndFlush(communityBoard);
//    CommunityBoardResponseDto communityBoardResponseDto = new CommunityBoardResponseDto().builder()
//        .title(communityBoard.getTitle())
//        .contents(communityBoard.getContents())
//        .build();
  }


  @Override
  @Transactional
  public CommunityBoardResponseDto getCommunityBoard(Long communityBoardId) {
    CommunityBoard communityBoard = boardRepository.findById(communityBoardId)
        .orElseThrow(() -> new CustomException(Status.NOT_FOUND_COMMUNITY_BOARD));
    Long likeCount = likeBoardRepository.countByBoard_Id(communityBoard.getId());//게시글의 좋아요


    List<CommunityWithLikeResponseDto> communityWithLikeResponseDtoList = new ArrayList<>();
    List<CommunityComment> communityCommentList = communityBoard.getCommunityComment();

    for (CommunityComment comment: communityCommentList) {
      Long commentLikeCount = likeCommentRepository.countByComment(comment);
      communityWithLikeResponseDtoList.add(new CommunityWithLikeResponseDto(comment, commentLikeCount));
    }

    CommunityBoardResponseDto communityBoardResponseDto = new CommunityBoardResponseDto().builder()
            .title(communityBoard.getTitle())
            .nickName(communityBoard.getNickName())
            .contents(communityBoard.getContents())
            .id(communityBoard.getId())
            .communityCommentsWithLike(communityWithLikeResponseDtoList)
            .boardLikeCount(likeCount)
            .build();

    return communityBoardResponseDto;
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponseDto<List<CommunityBoardResponseDto>> getAllCommunityBoard(int page, int size) {
    Sort sort = Sort.by(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<CommunityBoard> boards = boardRepository.findAll(pageable);


    List<CommunityBoardResponseDto> CommunityBoardResponseDtoList = new ArrayList<>();

    for (CommunityBoard communityBoard: boards) {
      List<CommunityWithLikeResponseDto> communityWithLikeResponseDtoList = new ArrayList<>();
      Long likeCount = likeBoardRepository.countByBoard(communityBoard);
      for (CommunityComment comment :communityBoard.getCommunityComment()) {
        long commentLike = likeCommentRepository.countByComment(comment);
        communityWithLikeResponseDtoList.add(new CommunityWithLikeResponseDto(comment,commentLike));
      }
      CommunityBoardResponseDtoList.add(new CommunityBoardResponseDto(communityBoard,likeCount,communityWithLikeResponseDtoList));
    }

    return new PageResponseDto<>(page, boards.getTotalElements(), CommunityBoardResponseDtoList);
  }
//  @Override
//  @Transactional
//  public PageResponseDto<List<CommunityBoardResponseDto>> getMyCommunityBoard(int page, int size, User user) {
//    Sort sort = Sort.by(Direction.ASC, "id");
//    Pageable pageable = PageRequest.of(page, size, sort);
//    Page<CommunityBoard> boards = boardRepository.findAllByNickName(pageable,user.getNickName());
//
//    List<CommunityBoardResponseDto> CommunityBoardResponseDtoList = new ArrayList<>();
//    for (CommunityBoard communityBoard: boards) {
//      Long likeCount = likeBoardRepository.countByBoard_Id(communityBoard.getId());
//      CommunityBoardResponseDto communityBoardResponseDto = new CommunityBoardResponseDto(communityBoard, likeCount,);
//      CommunityBoardResponseDtoList.add(communityBoardResponseDto);
//    }
////    List<CommunityBoardResponseDto> CommunityBoardResponseDtoList = boards.getContent()
////        .stream()
////        .map(CommunityBoardResponseDto::new)
////        .collect(Collectors.toList());
//    return new PageResponseDto<>(page, boards.getTotalElements(), CommunityBoardResponseDtoList);
//  }

//  @Override
//  @Transactional
//  public PageResponseDto<List<CommunityBoardResponseDto>> getMyCommunityBoard(int page, int size,
//      User user) {
//    Sort sort = Sort.by(Direction.ASC, "id");
//    Pageable pageable = PageRequest.of(page, size, sort);
//    Page<CommunityBoard> boards = boardRepository.findById(pageable,user.getId());
//    List<CommunityBoardResponseDto> CommunityBoardResponseDtoList = boards.getContent()
//        .stream()
//        .map(CommunityBoardResponseDto::new)
//        .collect(Collectors.toList());
//    return new PageResponseDto<>(page, boards.getTotalElements(), CommunityBoardResponseDtoList);
//  }

  @Override
  @Transactional
  public void deleteCommunityBoard(Long community_board_id,User user) {
    boardRepository.findByIdAndUser_NickName(community_board_id,user.getNickName())
        .orElseThrow(() -> new CustomException(Status.NOT_FOUND_COMMUNITY_BOARD));
    boardRepository.deleteById(community_board_id);
  }

}
