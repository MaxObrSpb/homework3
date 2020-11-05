package ru.digitalhabbits.homework3.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;
import ru.digitalhabbits.homework3.service.PersonService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PersonController.class)
@AutoConfigureRestDocs
class PersonControllerTest {

    @MockBean
    private PersonService personService;

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder().create();

    @Test
    @RequestMapping(consumes = "application/json", produces = "application/json")
    void persons() throws Exception {
        final PersonResponse response = buildPersonResponse();
        when(personService.findAllPersons()).thenReturn(List.of(response));
        mockMvc.perform(get("/api/v1/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(response.getId()))
                .andExpect(jsonPath("$[0].fullName").value(response.getFullName()))
                .andExpect(jsonPath("$[0].age").value(response.getAge()))
                .andExpect(jsonPath("$[0].department").value(response.getDepartment()))
                .andExpect(jsonPath("$[0].department.id").value(response.getDepartment().getId()))
                .andExpect(jsonPath("$[0].department.name").value(response.getDepartment().getName()))
                .andDo(document("persons",
                        responseFields(
                                fieldWithPath("[].id").description("id"),
                                fieldWithPath("[].fullName").description("fullName"),
                                fieldWithPath("[].age").description("age"),
                                fieldWithPath("[].department").description("department"),
                                fieldWithPath("[].department.id").description("id"),
                                fieldWithPath("[].department.name").description("name")
                        )));
    }

    @Test
    void person() throws Exception {
        final PersonResponse response = buildPersonResponse();
        when(personService.getPerson(response.getId())).thenReturn(response);
        mockMvc.perform(get("/api/v1/persons/" + response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.fullName").value(response.getFullName()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andExpect(jsonPath("$.department").value(response.getDepartment()))
                .andExpect(jsonPath("$.department.id").value(response.getDepartment().getId()))
                .andExpect(jsonPath("$.department.name").value(response.getDepartment().getName()))
                .andDo(document("person",
                        responseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("fullName").description("fullName"),
                                fieldWithPath("age").description("age"),
                                fieldWithPath("department").description("department"),
                                fieldWithPath("department.id").description("id"),
                                fieldWithPath("department.name").description("name")
                        )));
    }

    @Test
    void createPerson() throws Exception {
        final PersonRequest request = buildCreatePersonRequest();
        final PersonResponse response = buildPersonResponse(request);
        when(personService.createPerson(request)).thenReturn(response.getId());
        mockMvc.perform(post("/api/v1/persons")
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("createPerson",
                        requestFields(
                                fieldWithPath("firstName").description("First Name"),
                                fieldWithPath("middleName").description("Middle Name"),
                                fieldWithPath("lastName").description("Last Name"),
                                fieldWithPath("age").description("Age")
                        )));
    }

    @Test
    void updatePerson() throws Exception {
        final PersonRequest request = buildCreatePersonRequest();
        final PersonResponse response = buildPersonResponse(request);
        when(personService.updatePerson(response.getId(), request)).thenReturn(response);
        mockMvc.perform(patch("/api/v1/persons/" + response.getId())
                .content(gson.toJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.fullName").value(response.getFullName()))
                .andExpect(jsonPath("$.age").value(response.getAge()))
                .andDo(document("updatePerson",
                        requestFields(
                                fieldWithPath("firstName").description("First Name"),
                                fieldWithPath("middleName").description("Middle Name"),
                                fieldWithPath("lastName").description("Last Name"),
                                fieldWithPath("age").description("Age")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("fullName").description("fullName"),
                                fieldWithPath("age").description("age"),
                                fieldWithPath("department").description("department")
                        )));
    }

    @Test
    void deletePerson() throws Exception {
        final PersonResponse response = buildPersonResponse();
        personService.deletePerson(response.getId());
        mockMvc.perform(delete("/api/v1/persons/" + response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(document("deletePerson"));
    }

    public static PersonResponse buildPersonResponse(PersonRequest request) {
        return new PersonResponse()
                .setId(nextInt())
                .setFullName(request.getLastName() + " " + request.getFirstName() + " " + request.getMiddleName())
                .setAge(request.getAge());
    }

    public static PersonResponse buildPersonResponse() {
        return new PersonResponse()
                .setId(nextInt())
                .setAge(nextInt(10, 50))
                .setFullName(randomAlphabetic(8) + " " + randomAlphabetic(8) + " " + randomAlphabetic(8))
                .setDepartment(new DepartmentInfo().setId(nextInt()).setName(randomAlphabetic(8)));
    }

    public static PersonRequest buildCreatePersonRequest() {
        return new PersonRequest()
                .setAge(nextInt(10, 50))
                .setFirstName(randomAlphabetic(8))
                .setMiddleName(randomAlphabetic(8))
                .setLastName(randomAlphabetic(8));
    }
}