package com.yusufaksn.ticketsearch.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.yusufaksn.ticketsearch.document.TicketDocument;
import com.yusufaksn.ticketsearch.dto.TicketResponseDto;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<TicketResponseDto> searchTickets(String searchTerm, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        NativeQuery nativeQuery = NativeQuery.builder()
        .withQuery(q -> q.multiMatch(m -> m
                .fields("payload_*")
                .query(searchTerm)
                .fuzziness("AUTO")
                .lenient(true)   
                .type(TextQueryType.BestFields)
        ))
        .withPageable(pageable)
        .build();

        SearchHits<TicketDocument> searchHits = elasticsearchOperations.search(nativeQuery, TicketDocument.class);

        List<TicketResponseDto> responseList = searchHits.getSearchHits().stream()
                .map(hit -> TicketResponseDto.fromDocument(hit.getContent()))
                .toList();

        return new PageImpl<>(responseList, pageable, searchHits.getTotalHits());
    }
}