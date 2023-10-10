package me.sean.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.NotNull;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
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
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(false))
                .andDo(MockMvcRestDocumentation.document("create-event"))
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


    @ParameterizedTest()
    /*@CsvSource({
            "0,0,true",
            "100,0,false",
            "0,100,false"
    })*/
    @MethodSource("isFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        Event event = Event.builder().basePrice(basePrice).maxPrice(maxPrice).build();

        event.update();

        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Stream<Arguments> isFree() {
        return Stream.of(
                Arguments.of(0, 0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false)
        );
    }
   /* private Object[] ParamsTest(){
        return new Object[]{new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, true}
        };
    }*/

    @Test
    public void testOffline(){

        Event event = Event.builder().location("우리집").build();

        event.update();

        assertThat(event.isOffline()).isTrue();

        event = Event.builder().build();

        event.update();

        assertThat(event.isOffline()).isFalse();

    }
}
