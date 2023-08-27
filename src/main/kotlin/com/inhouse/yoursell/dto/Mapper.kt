package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.image.Image
import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import java.util.stream.Collectors

fun Role.toDto(): RoleDto{
    return RoleDto(
        name = name
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id=id,
        username=username,
        email=email,
        userRoles = userRoles.stream().map(Role::toDto).collect(Collectors.toSet()),
        vehicles = vehicles.stream().map(Vehicle::toDto).collect(Collectors.toList())
    )
}

fun Vehicle.toDto(): VehicleDto {
    return VehicleDto(
        id = id,
        make = make,
        model = model,
        mileage = mileage,
        vin = vin,
        year = year,
        expectedBid = expectedBid,
        damaged = damaged,
        sellerId = seller.id,
        images = images.stream().map(Image::toDto).collect(Collectors.toList()),
        deleted = deleted
    )
}

fun Image.toDto(): ImageDto {
    return ImageDto(
        id = id,
        name = name,
        contentType = contentType,
        size = size,
        vehicleId = vehicle.id
    )
}