package vigi.patient.presenter.service.appointment.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import vigi.patient.model.entities.Agenda;
import vigi.patient.model.services.Appointment;
import vigi.patient.model.services.Treatment;
import vigi.patient.presenter.service.appointment.api.AppointmentService;

public class FirebaseAppointmentService implements AppointmentService {

    List<Appointment> appointments;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceAppointment;
    private Query databaseQueryAppointment;

    @Override
    public void init(String currentPatientId) {
        appointments = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseQueryAppointment = firebaseDatabase.getReference(Appointment.class.getSimpleName())
        .orderByChild("patientId").equalTo(currentPatientId);

        databaseReferenceAppointment = firebaseDatabase.getReference(Appointment.class.getSimpleName());

    }

    @Override
    public void readAppointments(ValueEventListener listener) {
        addOnOperationCompleteListener(listener);

    }

    @Override
    public void setAllAppointments(List<Appointment> allAppointments) {
        this.appointments = allAppointments;

    }

    @Override
    public void setFirebaseAppointments(Context context, Appointment appointment) {
        String uuidNewAppointment = databaseReferenceAppointment.push().getKey();
        databaseReferenceAppointment.child(uuidNewAppointment).setValue(appointment).addOnCompleteListener(task -> {
            Toast.makeText(context,"Request has been added to the cart!", Toast.LENGTH_LONG).show();

        });

    }

    @Override
    public void addOnOperationCompleteListener(ValueEventListener valueEventListener) {
        databaseQueryAppointment.addValueEventListener(valueEventListener);

    }
}
