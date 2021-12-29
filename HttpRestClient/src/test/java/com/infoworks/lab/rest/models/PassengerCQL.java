package com.infoworks.lab.rest.models;

import com.it.soul.lab.cql.entity.CQLEntity;
import com.it.soul.lab.cql.entity.EnableTimeToLive;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.entity.Ignore;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.entity.TableName;
import com.it.soul.lab.sql.query.models.Property;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@TableName(value = "passenger_cql")
@EnableTimeToLive(300L) //TimeToLive 5*60 sec = 5 min
public class PassengerCQL extends CQLEntity {

	@PrimaryKey(name="uuid")
	private String uuid;

	//@ClusteringKey(name = "event_timestamp", order = Operator.ASC, type = DataType.LONG)
	private Long eventTimestamp = (new Date()).getTime();

    @NotNull(message = "name must not be null.")
    private String name;

    private String sex = Gender.NONE.name();

    @Min(value = 18, message = "age min Value is 18.")
	private int age = 18;

    private Long dob = new Date().getTime();
	private boolean active;

	@Ignore
	private long version = Long.MAX_VALUE;

	public PassengerCQL() {
	    this.uuid = UUID.randomUUID().toString();
    }

    public PassengerCQL(@NotNull(message = "Name must not be null") String name
            , Gender sex
            , @Min(value = 18, message = "Min Value is 18.") int age) {
        this();
	    this.name = name;
        this.sex = sex.name();
        this.age = age;
        updateDOB(age, false);
    }

    private void updateDOB(@Min(value = 18, message = "Min Value is 18.") int age, boolean isPositive) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Objects.nonNull(getDob()) ? new Date(getDob()) : new Date());
        int year = calendar.get(Calendar.YEAR) - ((isPositive) ? -age : age);
        calendar.set(Calendar.YEAR, year);
        setDob(calendar.getTime().getTime());
    }

    public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

	public Long getDob() {
		return dob;
	}

	public void setDob(Long dob) {
		this.dob = dob;
	}

	public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

	public Long getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(Long eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PassengerCQL passenger = (PassengerCQL) o;
		return Objects.equals(uuid, passenger.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	public Property getPropertyTest(String key, SQLExecutor exe, boolean skipPrimary) {
		return getProperty(key, exe, skipPrimary);

	}

}
