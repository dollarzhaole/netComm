package com.crscd.cds.ctc.filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author zhaole
 * @date 2022-04-18
 */
@XmlRootElement(name = "in_condition")
public class FilterRegister {
    @XmlElement(name = "rec")
    private ReceiveCondition receiveCondition;

    public void setReceiveCondition(ReceiveCondition receiveCondition) {
        this.receiveCondition = receiveCondition;
    }

    public static FilterRegister create(Collection<TypeFunc> typeFunc) {
        FilterRegister filterRegister = new FilterRegister();

        ReceiveCondition receiveCondition = new ReceiveCondition();
        ArrayList<String> conditions = new ArrayList<String>();
        for (TypeFunc func : typeFunc) {
            String str = String.format("0x%02X,0x%02X", func.getType(), func.getFunc());
            conditions.add(str);
        }
        receiveCondition.setConditions(conditions);
        filterRegister.setReceiveCondition(receiveCondition);

        return filterRegister;
    }

    public static class ReceiveCondition {
        @XmlElementWrapper(name = "protocol418_condition")
        @XmlElement(name = "type_func")
        private Collection<String> conditions;

        public void setConditions(Collection<String> conditions) {
            this.conditions = conditions;
        }
    }

    public static class TypeFunc {
        private short type;
        private short func;

        public short getType() {
            return type;
        }

        public short getFunc() {
            return func;
        }

        public static TypeFunc create(short type, short func) {
            TypeFunc tf = new TypeFunc();
            tf.type = type;
            tf.func = func;
            return tf;
        }
    }

    public String toXMLString() {
        try {
            JAXBContext context = JAXBContext.newInstance(this.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            StringWriter writer = new StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
