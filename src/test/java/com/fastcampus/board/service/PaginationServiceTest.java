package com.fastcampus.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("비즈니스 로직 - 페이지네이션")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class PaginationServiceTest {

    private final PaginationService sut;

    public PaginationServiceTest(@Autowired PaginationService paginationService) {
        this.sut = paginationService;
    }


    @DisplayName("현 페이지 번호와 총 페이지 수로 페이징 바 리스트 반환")
    @MethodSource
    @ParameterizedTest(name = "[{index}] 현재 페이지: {0}, 총 페이지: {1} => {2}")
    void test_paginationBar(int curPageNumb, int totalPageNumb, List<Integer> expected) {
        // Given current page number and total pages

        // When calculating
        List<Integer> actual = sut.getPaginationBarNumbers(curPageNumb, totalPageNumb);

        // Then returns pagination bar numbers
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> test_paginationBar() {
        return Stream.of(
                arguments(0, 13, List.of(0, 1, 2, 3, 4)),
                arguments(1, 13, List.of(0, 1, 2, 3, 4)),
                arguments(2, 13, List.of(0, 1, 2, 3, 4)),
                arguments(3, 13, List.of(1, 2, 3, 4, 5)),
                arguments(4, 13, List.of(2, 3, 4, 5, 6)),
                arguments(11, 13, List.of(9, 10, 11, 12)),
                arguments(12, 13, List.of(10, 11, 12))
        );
    }

    @DisplayName("현재 페이지네이션 바의 길이 반환")
    @Test
    void test_pageBarLength() {
        // Given nothing

        // When calling
        int barLength = sut.currentBarLength();

        // Then returns current bar length
        assertThat(barLength).isEqualTo(5);
    }

}