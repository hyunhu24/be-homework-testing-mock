package com.springboot.homework;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberPatchDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper mapper;

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberId = 1L;
        MemberDto.Patch patch = new MemberDto.Patch(
            memberId,
                "test1",
                "010-4512-7845",
                Member.MemberStatus.MEMBER_SLEEP
        );

        MemberDto.response response = new MemberDto.response(
                memberId,
                "test@test.com",
                "test1",
                "010-4512-7845",
                Member.MemberStatus.MEMBER_SLEEP,
                new Stamp()
        );

        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        String content = gson.toJson(patch);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(patch.getName()))
                .andExpect(jsonPath("$.data.phone").value(patch.getPhone()));
    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        //given
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(
                memberId,
                "test@test.com",
                "test",
                "010-1234-4512",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}").buildAndExpand(memberId).toUri();
        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(uri)
                        .accept(MediaType.APPLICATION_JSON)
        );
        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("wlsgur132@cat.com"))
                .andExpect(jsonPath("$.data.name").value("황아리"));

    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
//        Member member1 = new Member("test1@test.com", "test1" , "010-1111-1111");
//        member1.setStamp(new Stamp());
//        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
//        Member member2 = new Member("test2@test.com", "test2" , "010-2222-2222");
//        member2.setStamp(new Stamp());
//        member2.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        Page<Member> pageMembers = new PageImpl<>(List.of(new Member(), new Member()));

        List<MemberDto.response> responses = List.of(
                new MemberDto.response(
                        1L,
                        "test1@test.com",
                        "test1" ,
                        "010-1111-1111",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                ),
                new MemberDto.response(
                        2L,
                        "test2@test.com",
                        "test2" ,
                        "010-2222-2222",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        new Stamp()
                )
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        //path 설정
        MultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("page", "1");
        multiValueMap.add("size", "10");

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members").build().toUri();

        //ResultActions
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(uri)
                        .params(multiValueMap)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //검증
        MvcResult membersResult = actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        List list = JsonPath.parse(membersResult.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(), is(2));
    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberId = 1L;
        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}").buildAndExpand(memberId).toUri();
        doNothing().when(memberService).deleteMember(memberId);

        ResultActions deleteAction = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(uri)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        deleteAction.andExpect(status().isNoContent());

    }
}
