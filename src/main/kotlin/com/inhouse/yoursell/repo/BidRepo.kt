package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.bid.Bid
import com.inhouse.yoursell.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BidRepo : JpaRepository<Bid, UUID> {

    fun findByAuctionAndBidder(auction: Auction, bidder: User) : Optional<Bid>
}