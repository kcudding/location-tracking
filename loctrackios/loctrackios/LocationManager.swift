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
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest // Best accuracy
        locationManager.requestAlwaysAuthorization() // Always need location
        locationManager.startMonitoringSignificantLocationChanges()
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
    
    func sendRequest(long: Double, lat: Double, alt: Double){
        // TODO: UUID in url
        let time = Date.now
        let dateFmt = DateFormatter()
        dateFmt.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        let dateStr = dateFmt.string(from: time)
        @AppStorage("uuid") var uuid: String?
        
        if let uuid = uuid {
            let str = "?capturedate=\(dateStr)&id=\(uuid)&lat=\(lat)&long=\(lat)&alt=\(alt)"
            guard let url = URL(string: "https://multi-plier.ca/\(str)") else { return }
            let task = URLSession.shared.dataTask(with: url) {(data, response, error) in
                guard let data = data else { return }
                print(String(data: data, encoding: .utf8)!)
            }
            task.resume()
        }
    }


        func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            locationStatus = status
            print(#function, statusString)
        }
        
        func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
            guard let location = locations.last else { return }
            lastLocation = location
            let lat = lastLocation?.coordinate.latitude ?? -1
            let long = lastLocation?.coordinate.latitude ?? -1
            let alt = lastLocation?.altitude ?? -1
            sendRequest(long: long, lat: lat, alt: alt)
            print(#function, location)
        }
}
