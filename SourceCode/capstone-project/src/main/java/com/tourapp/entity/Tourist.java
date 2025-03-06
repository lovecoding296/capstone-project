package com.tourapp.entity;

import org.springframework.validation.annotation.Validated;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class Tourist{
	@Id
	private Long id;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "id")
	private Account account;
	private String preferences;
	
	

}
