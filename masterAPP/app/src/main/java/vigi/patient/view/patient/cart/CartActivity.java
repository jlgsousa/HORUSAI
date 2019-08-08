package vigi.patient.view.patient.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vigi.patient.R;
import vigi.patient.model.entities.CareProvider;
import vigi.patient.model.services.Appointment;
import vigi.patient.model.services.Treatment;
import vigi.patient.presenter.service.agenda.agenda.impl.AgendaConverter;
import vigi.patient.presenter.service.appointment.api.AppointmentService;
import vigi.patient.presenter.service.appointment.impl.AppointmentConverter;
import vigi.patient.presenter.service.appointment.impl.FirebaseAppointmentService;
import vigi.patient.presenter.service.careProvider.api.CareProviderService;
import vigi.patient.presenter.service.careProvider.firebase.CareProviderConverter;
import vigi.patient.presenter.service.careProvider.firebase.FirebaseCareProviderService;
import vigi.patient.presenter.service.treatment.api.TreatmentService;
import vigi.patient.presenter.service.treatment.impl.firebase.FirebaseTreatmentService;
import vigi.patient.presenter.service.treatment.impl.firebase.TreatmentConverter;
import vigi.patient.view.patient.cart.viewHolder.CartAdapter;
import vigi.patient.view.utils.recyclerView.EmptyRecyclerView;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("FieldCanBeLocal")
public class CartActivity extends AppCompatActivity {

    private String TAG = getClass().getName();
    private List<Appointment> appointmentsList, appointmentsInCart;
    private TextView totalPrice;
    private EmptyRecyclerView.LayoutManager layoutManager;
    private EmptyRecyclerView recyclerView;
    private List<CareProvider> careProvidersList;
    private List<Treatment> treatmentList;
    private AppointmentService appointmentService;
    private CareProviderService careProviderService;
    private TreatmentService treatmentService;
    private ValueEventListener careProviderListener, appointmentListener, treatmentListener;
    private CartAdapter cartAdapter;
    private List<String> careProviderIds, treatmentsIds, appointmentsIds;
    private RelativeLayout detailsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar_cart);

        String currentPatientId = "1"; //TODO change with currentPatientTokenId
        appointmentsList = new ArrayList<>();
        careProvidersList = new ArrayList<>();
        treatmentList = new ArrayList<>();

        customizeActionBar();
        customizeToolBar();

        setUpServices(currentPatientId);
    }

    private void setUpServices(String currentPatientId) {

        recyclerView = findViewById(R.id.recycler_view);
        detailsLayout = findViewById(R.id.confirmation_layout);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        appointmentListener = new CartActivity.AppointmentValueEventListener();
        appointmentService = new FirebaseAppointmentService();
        appointmentService.init(currentPatientId);

        careProviderListener = new CartActivity.CareProviderValueEventListener();
        careProviderService = new FirebaseCareProviderService();
        careProviderService.init();

        treatmentListener = new CartActivity.TreatmentValueEventListener();
        treatmentService = new FirebaseTreatmentService();
        treatmentService.init();

        appointmentService.readAppointments(appointmentListener);
    }

    private void notifyAppointmentDataChanged(List<Appointment> appointmentList){
        appointmentService.setAllAppointments(appointmentList);
    }

    private void customizeActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void customizeToolBar() {
        checkNotNull(getSupportActionBar());
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Treatments in the cart");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public class AppointmentValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            appointmentsList = new ArrayList<>();
            for (DataSnapshot snapshotAppointment : dataSnapshot.getChildren()) {
                if (snapshotAppointment.child("status").getValue().equals("cart")){
                    appointmentsList.add(AppointmentConverter.getAppointmentFromDataSnapshot(snapshotAppointment));
                }
            }

            careProviderService.readCareProviders(careProviderListener);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }


    public class CareProviderValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            careProvidersList = new ArrayList<>();
            careProviderIds = appointmentsList.stream().map(appointment -> appointment.getCareProviderId()).collect(toList());

            for (DataSnapshot snapshotCareProvider : dataSnapshot.getChildren()) {
                if(careProviderIds.contains(snapshotCareProvider.child("id").getValue().toString())){
                    careProvidersList.add(CareProviderConverter.getCareProviderFromDataSnapshot(snapshotCareProvider));
                }
            }
            treatmentService.readTreatments(treatmentListener);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

    public class TreatmentValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            treatmentList = new ArrayList<>();
            treatmentsIds = appointmentsList.stream().map(appointment -> appointment.getTreatmentId()).collect(toList());

            for (DataSnapshot snapshotTreatmentInstance : dataSnapshot.getChildren()) {
                if(treatmentsIds.contains(snapshotTreatmentInstance.child("id").getValue())){
                    treatmentList.add(TreatmentConverter.getTreatmentFromDataSnapshot(snapshotTreatmentInstance));
                }
            }

            cartAdapter = new CartAdapter(appointmentsList, careProvidersList, treatmentList, detailsLayout::setVisibility);
            recyclerView.setAdapter(cartAdapter);
            recyclerView.setEmptyView(findViewById(R.id.empty_view)); // Can only be called after setting adapter


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return false;
    }
}