package com.nailshop.nailborhood.domain.alarm;

import com.nailshop.nailborhood.domain.common.BaseTime;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.type.AlarmType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Alarm extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId;

    //TODO: alarm 맵핑

    // 멤버 id
    // 알람 수신자
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    // 알람 발신자
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    // Code -> 리뷰 알림, 채팅 알림, 좋아요 알림, 구별 코드
    // status 형식으로 설정하는게 낫지 않을까?
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    // checked -> 알림 수신 확인
    private boolean isChecked;

    private String url;

    // 어디서 왔는지? 필요할까?
    // private String prefix;


    @Builder
    public Alarm(Member receiver, Member sender, AlarmType alarmType, boolean isChecked, String url) {
        this.receiver = receiver;
        this.sender = sender;
        this.alarmType = alarmType;
        this.isChecked = isChecked;
        this.url = url;
    }

    public void updateIsChecked(Boolean isChecked){
        this.isChecked = isChecked;
    }
}
