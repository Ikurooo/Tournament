import {Component, OnInit} from '@angular/core';
import {ToastrService} from "ngx-toastr";
import {TournamentService} from "../../service/tournament.service";
import {debounceTime, Subject} from "rxjs";
import {TournamentListDto, TournamentSearchParams} from "../../dto/tournament";
import {NgForOf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-tournament',
  templateUrl: './tournament.component.html',
  styleUrls: ['./tournament.component.scss'],
  imports: [
    NgForOf,
    RouterLink,
    FormsModule
  ],
  standalone: true
})

export class TournamentComponent implements OnInit {

  search = false;
  tournaments: TournamentListDto[] = [];
  bannerError: string | null = null;
  searchParams: TournamentSearchParams = {};
  searchBeginEarliest: string | null = null;
  searchEndLatest: string | null = null;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) {
  }

  ngOnInit(): void {
    this.reloadTournaments();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadTournaments()});
  }

  reloadTournaments() {
    if (this.searchBeginEarliest == null || this.searchEndLatest === "") {
      delete this.searchParams.startDate;
    } else {
      this.searchParams.startDate = new Date(this.searchBeginEarliest);
    }
    if (this.searchEndLatest == null || this.searchEndLatest === "") {
      delete this.searchParams.endDate;
    } else {
      this.searchParams.endDate = new Date(this.searchEndLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.tournaments = data.map(tournament => ({
            ...tournament,
            startDate: new Date(tournament.startDate), // Convert startDate string to Date
            endDate:
              new Date(tournament.endDate)     // Convert endDate string to Date
          }))
        },
        error: error => {
          console.error('Error fetching tournaments', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Tournaments');
        }
      });
  }

  searchChanged()
    :
    void {
    this.searchChangedObservable.next();
  }
}
