package com.example.app.email;

import com.example.app.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "email_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResponse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotBlank
	@Column(nullable = false)
	private String subject;

	@Lob
	@Column(nullable = false)
	private String body;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@PrePersist
	public void onCreate() {
		if (createdAt == null) {
			createdAt = Instant.now();
		}
	}
}