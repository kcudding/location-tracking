//
//  ContentView.swift
//  loctrackios
//
//  Created by thaqib on 2022-07-30.
//

import SwiftUI


struct LocationView: View {
    @StateObject var locationManager = LocationManager()
    var userLatitude: String {
        return "\(locationManager.lastLocation?.coordinate.latitude ?? 0)"
        }
    var userLongitude: String {
           return "\(locationManager.lastLocation?.coordinate.longitude ?? 0)"
       }
    
    var userAltitude: String {
        return "\(locationManager.lastLocation?.altitude ?? 0)"
    }
    
    @AppStorage("name") var uuid: String?
    
    var body: some View {
        VStack {
            Text("location status: \(locationManager.statusString)")
            Text("latitude: \(userLatitude)")
            Text("longitude: \(userLongitude)")
            Text("altitude: \(userAltitude)")
            Button(action: {
                let str = "\(Date.now.formatted().hashValue)"
                UserDefaults.standard.set(str, forKey: "uuid")
                uuid = str
            }){
                Text("Start Tracking")
                Image(systemName: "location")
            }
            .disabled(uuid != nil)
            Text("uuid: \(uuid ?? "not set")")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        LocationView()
    }
}
