package com.wm.beans;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class HESUser 
{
	@JsonProperty("id")
    private String id;
	@JsonProperty("firstName")
    private String firstName;
	@JsonProperty("lastName")
    private String lastName;
    @JsonProperty("hesClockedIn")
    private String hesClockedIn;
    @JsonProperty("hesClockedInTS")
    private String hesClockedInTS;
    private HESUser() {
        // private constructor to enforce the use of the builder
    }

    public static HESUserBuilder builder() {
        return new HESUserBuilder();
    }

    // getters and other methods...

    public static class HESUserBuilder {
        private HESUser user;

        private HESUserBuilder() {
            this.user = new HESUser();
        }

        public HESUserBuilder id(String id) {
            user.id = id;
            return this;
        }

        public HESUserBuilder firstName(String firstName) {
            user.firstName = firstName;
            return this;
        }

        public HESUserBuilder lastName(String lastName) {
            user.lastName = lastName;
            return this;
        }

        public HESUserBuilder hesClockedIn(String hesClockedIn) {
            user.hesClockedIn = hesClockedIn;
            return this;
        }

        public HESUserBuilder hesClockedInTS(String hesClockedInTS) {
            user.hesClockedInTS = hesClockedInTS;
            return this;
        }

        public HESUser build() {
            // Perform any additional checks or processing before returning the built object
            return user;
        }
    }
}