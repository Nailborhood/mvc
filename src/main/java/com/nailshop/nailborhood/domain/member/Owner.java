package com.nailshop.nailborhood.domain.member;

import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.shop.Shop;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long ownerId;

    private boolean isApproved;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToMany(mappedBy = "owner")
    private List<ChattingRoom> chattingRoomList;

    @Builder
    public Owner(boolean isApproved, Member member, Shop shop) {
        this.isApproved = isApproved;
        this.member = member;
        this.shop = shop;
    }

    public void changeIsApproved(boolean isApproved){
        this.isApproved = isApproved;
    }
}
