//
//  LocationManager.swift
//  loctrackios
//
//  Created by thaqib on 2022-07-30.
//

import Foundation
import CoreLocation
import Combine
import SwiftUI

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    @Published var locationStatus: CLAuthorizationStatus?
    @Published var lastLocation: CLLocation?
    var lastUpdateTime: Date
    let timeInterval: TimeInterval = 10.0 // Update time interval
    override init() {
        lastUpdateTime = Date.now
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest // Best accuracy
        locationManager.requestAlwaysAuthorization() // Always need location
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.startUpdatingLocation()

    }
    var statusString: String {
            guard let status = locationStatus else {
                return "unknown"
            }
            
            switch status {
            case .notDetermined:
                return "notDetermined"
            case .authorizedWhenInUse: return "authorizedWhenInUse"
            case .authorizedAlways: return "authorizedAlways"
            case .restricted: return "restricted"
            case .denied: return "denied"
            default: return "unknown"
            }
        }
    
    func requestPermission(){
        locationManager.requestAlwaysAuthorization() // Always need location
        locationManager.allowsBackgroundLocationUpdates = true
    }
    func sendRequest(long: Double, lat: Double, alt: Double, lastUpdateTime: Date) -> Date{
        // TODO: UUID in url
        let time = Date.now
        let dateFmt = DateFormatter()
        dateFmt.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        let dateStr = dateFmt.string(from: time)
        @AppStorage("uuid") var uuid: String?
        
        if let uuid = uuid {
            let str = "?capturedate=\(dateStr)&id=\(uuid)&lat=\(lat)&long=\(long)&alt=\(alt)"
            guard let url = URL(string: "https://multi-plier.ca/\(str)") else { return lastUpdateTime }
            let task = URLSession.shared.dataTask(with: url) {(data, response, error) in
                guard let data = data else { return }
                print(String(data: data, encoding: .utf8)!)
            }
            task.resume()
            return time
        }
        return lastUpdateTime
    }


        func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            locationStatus = status
            print(#function, statusString)
        }
        
        func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
            guard let location = locations.last else { return }
            lastLocation = location
            let lat = lastLocation?.coordinate.latitude ?? -1
            let long = lastLocation?.coordinate.longitude ?? -1
            let alt = lastLocation?.altitude ?? -1
            //sendRequest(long: long, lat: lat, alt: alt)
            let delta = Date.now.timeIntervalSince1970 - lastUpdateTime.timeIntervalSince1970
            print(#function, "Time Since last update: \(delta)s")
            if(delta > timeInterval){ // Update every 10 seconds
                lastUpdateTime = sendRequest(long: long, lat: lat, alt: alt, lastUpdateTime: lastUpdateTime)
            }
            print(#function, location)
        }
}
