package me.sean.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        Event event = new Event();
        String name = "sean";
        event.setName(name);
        String description = "spring boot";
        event.setDescription(description);

        assertThat(event.getName().equals(name));
        assertThat(event.getDescription()).isEqualTo(description);
    }
}