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
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.*;
import ru.digitalhabbits.homework3.service.DepartmentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DepartmentController.class)
@AutoConfigureRestDocs
class DepartmentControllerTest {

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder().create();

    @Test
    void departments() throws Exception {
        DepartmentShortResponse departmentResponse = buildDepartmentShortResponse();
        when(departmentService.findAllDepartments()).thenReturn(List.of(departmentResponse));
        mockMvc.perform(get("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(departmentResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(departmentResponse.getName()))
                .andDo(document("departments",
                        responseFields(
                                fieldWithPath("[].id").description("id"),
                                fieldWithPath("[].name").description("name")
                        )));
    }

    @Test
    void department() throws Exception {
        DepartmentResponse departmentResponse = buildDepartmentResponse();
        when(departmentService.getDepartment(departmentResponse.getId())).thenReturn(departmentResponse);
        mockMvc.perform(get("/api/v1/departments/" + departmentResponse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(departmentResponse.getId()))
                .andExpect(jsonPath("$.name").value(departmentResponse.getName()))
                .andExpect(jsonPath("$.persons").isArray())
                .andExpect(jsonPath("$.persons").value(departmentResponse.getPersons()));
    }

    @Test
    void createDepartment() throws Exception {
        final Department department = buildDepartment();
        final DepartmentRequest request = buildDepartmentRequest();
        request.setName(department.getName());

        final DepartmentShortResponse response = buildDepartmentShortResponse(department);
        when(departmentService.createDepartment(request)).thenReturn(response.getId());

        mockMvc.perform(post("/api/v1/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void updateDepartment() throws Exception {
        final Department department = buildDepartment();
        final DepartmentRequest request = buildDepartmentRequest();
        request.setName(department.getName());
        final DepartmentResponse response = buildDepartmentResponse(department);
        when(departmentService.updateDepartment(department.getId(), request)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/departments/" + response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()))
                .andExpect(jsonPath("$.persons").isArray())
                .andExpect(jsonPath("$.persons").value(response.getPersons()));
    }

    @Test
    void deleteDepartment() throws Exception {
        final Department department = buildDepartment();
        final DepartmentResponse response = buildDepartmentResponse(department);
        doNothing().when(departmentService).deleteDepartment(department.getId());

        mockMvc.perform(delete("/api/v1/departments/" + response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void addPersonToDepartment() throws Exception {
        final Department department = buildDepartment();
        final Person person = buildPerson();
        doNothing().when(departmentService).addPersonToDepartment(department.getId(), person.getId());
        mockMvc.perform(post("/api/v1/departments/" + department.getId() + "/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void removePersonToDepartment() throws Exception {
        final Department department = buildDepartment();
        final Person person = buildPerson();
        doNothing().when(departmentService).removePersonToDepartment(department.getId(), person.getId());
        mockMvc.perform(delete("/api/v1/departments/" + department.getId() + "/" + person.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void closeDepartment() throws Exception {
        final Department department = buildDepartment();
        doNothing().when(departmentService).closeDepartment(department.getId());

        mockMvc.perform(post("/api/v1/departments/" + department.getId() + "/close")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    public static DepartmentShortResponse buildDepartmentShortResponse() {
        return new DepartmentShortResponse()
                .setId(nextInt())
                .setName(randomAlphabetic(8));
    }

    public static DepartmentResponse buildDepartmentResponse() {
        return new DepartmentResponse()
                .setId(nextInt())
                .setName(randomAlphabetic(8))
                .setClosed(false)
                .setPersons(new ArrayList<>());
    }

    public static Department buildDepartment() {
        return new Department()
                .setId(nextInt())
                .setName(randomAlphabetic(7))
                .setPeople(new ArrayList<>())
                .setClosed(false);
    }

    public static DepartmentRequest buildDepartmentRequest() {
        return new DepartmentRequest()
                .setName(randomAlphabetic(7));
    }

    public static DepartmentShortResponse buildDepartmentShortResponse(Department department) {
        return new DepartmentShortResponse()
                .setId(department.getId())
                .setName(department.getName());
    }

    public static DepartmentResponse buildDepartmentResponse(Department department) {
        List<PersonInfo> personInfoList = department.getPeople()
                .stream()
                .map(DepartmentControllerTest::buildPersonInfo)
                .collect(Collectors.toList());

        return new DepartmentResponse()
                .setId(department.getId())
                .setName(department.getName())
                .setClosed(department.isClosed())
                .setPersons(personInfoList);
    }

    public static PersonInfo buildPersonInfo(Person person) {
        return new PersonInfo()
                .setId(person.getId())
                .setFullName(person.getFirstName() + " " + person.getLastName() + " " + person.getMiddleName());
    }

    public static Person buildPerson() {
        return new Person()
                .setId(nextInt())
                .setFirstName(randomAlphabetic(7))
                .setMiddleName(randomAlphabetic(7))
                .setLastName(randomAlphabetic(7))
                .setAge(nextInt(10, 50));
    }
}