package com.example.community.controller;

import com.example.community.domain.board.BoardEntity;
import com.example.community.domain.post.PostEntity;
import com.example.community.persistence.BoardRepository;
import com.example.community.persistence.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MainController 통합 테스트
 * - @SpringBootTest: 전체 스프링 컨텍스트 로딩
 * - H2(테스트 DB)에 데이터를 넣고 실제 서비스/레포지토리까지 포함해 검증
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class MainControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    PostRepository postRepository;

//    테스트 실행 전 데이터 초기화
    @BeforeEach
    void setUp() {
        log.info("데이터 초기화");

        postRepository.deleteAll();
        boardRepository.deleteAll();

//        공지 카테고리 게시판 생성
        BoardEntity noticeBoard = boardRepository.save(
                BoardEntity.builder().title("커뮤니티 공지").build()
        );

//        일반 게시판 생성
        boardRepository.save(
                BoardEntity.builder().title("자유게시판").build()
        );

//        공지 게시글 생성
        postRepository.saveAll(List.of(
                PostEntity.builder()
                        .userId(1L)
                        .boardId(noticeBoard.getId())
                        .title("공지글1")
                        .content("c1")
                        .build(),
                PostEntity.builder()
                        .userId(1L)
                        .boardId(noticeBoard.getId())
                        .title("공지글2")
                        .content("c2")
                        .build()
        ));

        log.info("데이터 초기화 완료");
    }

    @Test
    void index_shouldSetSidebarAndNoticeModels() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("noticeBoardList"))
                .andExpect(model().attributeExists("boardList"))
                .andExpect(model().attributeExists("noticeList"))
                // 공지 카테고리 1개, 일반 게시판 1개
                .andExpect(model().attribute("noticeBoardList", hasSize(1)))
                .andExpect(model().attribute("boardList", hasSize(1)))
                // 공지글 2개가 noticeList로 들어와야 함
                .andExpect(model().attribute("noticeList", hasSize(2)));

        log.info("공지 카테고리 존재");
    }

    // 공지 카테고리가 없을 경우에 대한 테스트
    @Test
    void index_whenNoNoticeBoards_noticeListShouldBeEmpty() throws Exception {
        postRepository.deleteAll();
        boardRepository.deleteAll();

        boardRepository.save(BoardEntity.builder().title("자유게시판").build());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("noticeBoardList", hasSize(0)))
                .andExpect(model().attribute("boardList", hasSize(1)))
                .andExpect(model().attribute("noticeList", hasSize(0)));

        log.info("공지 카테고리 없음");
    }
}
