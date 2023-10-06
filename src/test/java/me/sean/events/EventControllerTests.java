package me.sean.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    @NotNull
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spting")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,10,01,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,10,21,14,21))
                .beginEventDateTime(LocalDateTime.of(2023,10,11,14,21))
                .endEventDateTime(LocalDateTime.of(2023,10,11,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("우리집")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.BEGAN_ENROLLMENT)
                .build();
        Mockito.when(eventRepository.save(any(Event.class))).thenReturn(event);
        //Mockito.when(eventRepository.save(event)).thenReturn(event);
        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                //.andExpect(jsonPath("free").value(Matchers.not(false)))
        ;
    }

    @Test
    @NotNull
    public void createEvent_bad_request() throws Exception {
        Event event = Event.builder()
                .id(10)
                .name("Spting")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,10,01,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,10,21,14,21))
                .beginEventDateTime(LocalDateTime.of(2023,10,11,14,21))
                .endEventDateTime(LocalDateTime.of(2023,10,11,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("우리집")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.BEGAN_ENROLLMENT)
                .build();
        Mockito.when(eventRepository.save(any(Event.class))).thenReturn(event);
        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())

        ;
    }

    @Test
    public void createEvent_Bad_request_Empty() throws Exception {

        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEcent_Bad_request_Wrong_intput() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spting")
                .description("description")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,10,01,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,10,21,14,21))
                .beginEventDateTime(LocalDateTime.of(2023,10,11,14,21))
                .endEventDateTime(LocalDateTime.of(2023,9,11,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("우리집")
                .build();

        this.mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }
}
