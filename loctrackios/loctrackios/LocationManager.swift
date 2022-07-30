//
//  LocationManager.swift
//  loctrackios
//
//  Created by thaqib on 2022-07-30.
//

import Foundation
import CoreLocation
import Combine

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    @Published var locationStatus: CLAuthorizationStatus?
    @Published var lastLocation: CLLocation?
    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest // Best accuracy
        locationManager.requestAlwaysAuthorization() // Always need location
        locationManager.startUpdatingLocation()
    }
    var statusString: String {
            guard let status = locationStatus else {
                return "unknown"
            }
            
            switch status {
            case .notDetermined: return "notDetermined"
            case .authorizedWhenInUse: return "authorizedWhenInUse"
            case .authorizedAlways: return "authorizedAlways"
            case .restricted: return "restricted"
            case .denied: return "denied"
            default: return "unknown"
            }
        }
    
    func sendRequest(long: Double, lat: Double){
        let str = "?capturedate=0&id=iosEmulator&lat=\(lat)&long=\(lat)&alt=-1"
        guard let url = URL(string: "https://multi-plier.ca/\(str)") else { return }
        let task = URLSession.shared.dataTask(with: url) {(data, response, error) in
            guard let data = data else { return }
            print(String(data: data, encoding: .utf8)!)
        }
        task.resume()
    }


        func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            locationStatus = status
            print(#function, statusString)
        }
        
        func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
            guard let location = locations.last else { return }
            lastLocation = location
            // TEST REMOVE LATER (SENDS AT EVERY LOCATION UPDATE)
            // TODO:
            sendRequest(long: (lastLocation?.coordinate.latitude)!, lat: (lastLocation?.coordinate.latitude)!)
            print(#function, location)
        }
}
