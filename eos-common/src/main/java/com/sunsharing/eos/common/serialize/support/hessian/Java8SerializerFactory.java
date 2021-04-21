package com.sunsharing.eos.common.serialize.support.hessian;


import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.ExtSerializerFactory;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.sunsharing.eos.common.serialize.support.hessian.java8.DurationHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.InstantHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.LocalDateHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.LocalDateTimeHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.LocalTimeHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.MonthDayHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.OffsetDateTimeHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.OffsetTimeHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.PeriodHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.YearHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.YearMonthHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.ZoneIdSerializer;
import com.sunsharing.eos.common.serialize.support.hessian.java8.ZoneOffsetHandle;
import com.sunsharing.eos.common.serialize.support.hessian.java8.ZonedDateTimeHandle;

import static com.sunsharing.eos.common.serialize.support.hessian.java8.Java8TimeSerializer.create;


public class Java8SerializerFactory extends ExtSerializerFactory {
    public static final AbstractSerializerFactory INSTANCE = new Java8SerializerFactory();

    private Java8SerializerFactory() {
        if (isJava8()) {
            try {
                this.addSerializer(Class.forName("java.time.LocalTime"), create(LocalTimeHandle.class));
                this.addSerializer(Class.forName("java.time.LocalDate"), create(LocalDateHandle.class));
                this.addSerializer(Class.forName("java.time.LocalDateTime"), create(LocalDateTimeHandle.class));

                this.addSerializer(Class.forName("java.time.Instant"), create(InstantHandle.class));
                this.addSerializer(Class.forName("java.time.Duration"), create(DurationHandle.class));
                this.addSerializer(Class.forName("java.time.Period"), create(PeriodHandle.class));

                this.addSerializer(Class.forName("java.time.Year"), create(YearHandle.class));
                this.addSerializer(Class.forName("java.time.YearMonth"), create(YearMonthHandle.class));
                this.addSerializer(Class.forName("java.time.MonthDay"), create(MonthDayHandle.class));

                this.addSerializer(Class.forName("java.time.OffsetDateTime"), create(OffsetDateTimeHandle.class));
                this.addSerializer(Class.forName("java.time.ZoneOffset"), create(ZoneOffsetHandle.class));
                this.addSerializer(Class.forName("java.time.OffsetTime"), create(OffsetTimeHandle.class));
                this.addSerializer(Class.forName("java.time.ZonedDateTime"), create(ZonedDateTimeHandle.class));
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
    }

    @Override
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        return isZoneId(cl) ? ZoneIdSerializer.getInstance() : super.getSerializer(cl);
    }

    private static boolean isZoneId(Class cl) {
        try {
            return isJava8() && Class.forName("java.time.ZoneId").isAssignableFrom(cl);
        } catch (ClassNotFoundException e) {
            // ignore
        }
        return false;
    }

    private static boolean isJava8() {
        String javaVersion = System.getProperty("java.specification.version");
        return Double.valueOf(javaVersion) >= 1.8;
    }
}
