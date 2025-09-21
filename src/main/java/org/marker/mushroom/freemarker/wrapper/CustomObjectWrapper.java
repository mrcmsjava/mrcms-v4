package org.marker.mushroom.freemarker.wrapper;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleDate;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CustomObjectWrapper extends DefaultObjectWrapper {

    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj instanceof LocalDateTime) {
            Timestamp timestamp = Timestamp.valueOf((LocalDateTime) obj);
            return new SimpleDate(timestamp);
        }
        if (obj instanceof LocalDate) {
            Date date = Date.valueOf((LocalDate) obj);
            return new SimpleDate(date);
        }
        if (obj instanceof LocalTime) {
            Time time = Time.valueOf((LocalTime) obj);
            return new SimpleDate(time);
        }
        return super.wrap(obj);
    }
}
