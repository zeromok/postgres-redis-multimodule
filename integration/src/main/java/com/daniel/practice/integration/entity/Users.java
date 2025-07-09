package com.daniel.practice.integration.entity;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.daniel.practice.integration.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/// 사용자 정보를 저장하는 Entity
///
/// PostgresSQL 의 JSONB 타입을 활용하여 다양한 providerInfo 의 정보를 저장함
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter @Setter
public class Users extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String name;

	/// OAuth 프로바이더별 추가 정보를 JSON 형태로 저장
	///
	/// JdbcTypeCode(Types.JSON):  Hibernate 6+ 방식의 JSON 매핑
	///
	/// columnDefinition = "jsonb": PostgreSQL의 JSONB 타입 사용 명시
	///
	/// JSONB vs JSON:
	/// - JSONB: 바이너리 형태로 압축 저장, 빠른 검색, 인덱싱 가능
	/// - JSON: 텍스트 그대로 저장, 입력 순서 유지
	///
	/// 저장 예시:
	///
	/// Google: {"sub": "123", "picture": "url", "locale": "ko_KR"}
	///
	/// GitHub: {"id": 456, "avatar_url": "url", "location": "Seoul"}
	@Setter
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> providerInfo;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private UserStatus status = UserStatus.ACTIVE;

	public boolean isActive() {
		return status == UserStatus.ACTIVE;
	}
}
