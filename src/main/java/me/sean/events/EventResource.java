package me.sean.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

//public class EventResource extends RepresentationModel{
public class EventResource extends EntityModel<Event> {
    protected EventResource(Event content, Iterable<Link> links) {
        super(content, links);
    }

    @Override
    public EntityModel<Event> add(Link link) {
        return super.add(link);
    }

    protected EventResource(Event content) {
        super(content);
    }
    /*  @JsonUnwrapped
    private Event event;

    public EventResource(Event event){
        this.event = event;
    }
    public Event getEvent() {
        return event;
    }*/
}
