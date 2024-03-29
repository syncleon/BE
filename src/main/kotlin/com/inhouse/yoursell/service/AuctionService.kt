package com.inhouse.yoursell.service

import com.inhouse.yoursell.config.toUser
import com.inhouse.yoursell.dto.*
import com.inhouse.yoursell.entity.auction.Auction
import com.inhouse.yoursell.entity.auction.AuctionStatus
import com.inhouse.yoursell.exceptions.NotFoundException
import com.inhouse.yoursell.repo.AuctionRepo
import com.inhouse.yoursell.repo.VehicleRepo
import jakarta.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


@Service
@Transactional
class AuctionService (
    private val auctionRepo: AuctionRepo,
    private val vehicleRepo: VehicleRepo
) {
    fun createAuction(
        authentication: Authentication,
        payload: CreateAuctionDto
    ): AuctionDto {
        val vehicleId = payload.vehicleId
        val duration = payload.duration
        val authUser = authentication.toUser()

        val vehicle = vehicleRepo.findByIdAndSeller(vehicleId, authUser).orElseThrow {
            throw NotFoundException("Vehicle not found")
        }
        val timestampMinute = 60 * 1000L
        val timestampHour = 60 * 60 * 1000L
        val timestampDay = 24 * 60 * 60 * 1000L
        val timestampWeek = 7 * 24 * 60 * 60 * 1000L
        val timestampMonth = 30 * 24 * 60 * 60 * 1000L

        val startTime = System.currentTimeMillis()
        val endTime: Long = when (duration) {
            "minute" -> {
                startTime + timestampMinute
            }
            "hour" -> {
                startTime + timestampHour
            }
            "day" -> {
                startTime + timestampDay
            }
            "week" -> {
                startTime + timestampWeek
            }

            "month" -> {
                startTime + timestampMonth
            }

            else -> throw IllegalArgumentException("Unsupported duration: ${payload.duration}")
        }
        val auction = Auction(
            auctionOwner = authUser,
            vehicle = vehicle,
            auctionStatus = AuctionStatus.STARTED,
            reservePrice = payload.reservePrice,
            startTime = startTime,
            endTime = endTime
        )
        vehicle.onSale = true

        vehicleRepo.save(vehicle)

        return auctionRepo.save(auction).toDto()
    }

    fun closeAuctions() {
        val currentTime = System.currentTimeMillis()
        val expiredAuctions = auctionRepo.findByEndTimeLessThanAndAuctionStatus(currentTime, AuctionStatus.STARTED)
        expiredAuctions.forEach { auction ->
            auction.auctionStatus = AuctionStatus.CLOSED
            val vehicle = vehicleRepo.findById(auction.vehicle.id).orElseThrow {
                throw Exception("Vehicle not found.")
            }
            vehicle.onSale = false
            vehicle.isSold = true
            vehicleRepo.save(vehicle)
            auctionRepo.save(auction).toDto()
        }
    }

    fun findAll(): MutableList<AuctionDto> {
        val auctionList = auctionRepo.findAll()
        val auctionsDtoList: MutableList<AuctionDto> = mutableListOf()
        auctionList.forEach { auction ->
            val auctionDto = auction.toDto()
            auctionsDtoList.add(auctionDto)
        }
        return auctionsDtoList
    }

    fun findById(id: UUID): AuctionDto {
        val auction = auctionRepo.findById(id).orElseThrow {
            throw NotFoundException("Auction not found.")
        }

        var maxBid: BigDecimal = BigDecimal.ZERO
        var maxBidderId = 0L

        for (bid in auction.bids) {
            if (bid.bidValue > maxBid) {
                maxBid = bid.bidValue
                maxBidderId = bid.bidder.id
            }
        }

        auction.currentMaxBid = maxBid
        auction.currentMaxBidderId = maxBidderId

        return auction.toDto()
    }
}



