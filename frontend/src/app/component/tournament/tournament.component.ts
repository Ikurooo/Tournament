// Import necessary Angular modules and components
import { Component, OnInit } from '@angular/core';
import {AppModule} from "../../app.module";
import {FormsModule} from "@angular/forms";
import {NgForOf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {TournamentListDto, TournamentSearchParams} from "../../dto/tournament";
import {ToastrService} from "ngx-toastr";
import {TournamentService} from "../../service/tournament.service";

@Component({
    selector: 'app-tournament',
    templateUrl: './tournament.component.html',
    styleUrls: ['./tournament.component.scss'],
    standalone: true,
    imports: [
        AppModule,
        FormsModule,
        NgForOf,
        RouterLink
    ],
})
export class TournamentComponent implements OnInit {

  search = false;
  tournaments: TournamentListDto[] = [];
  bannerError: string | null = null;
  searchParams: TournamentSearchParams = {};
  searchBeginEarliest: string | null = null;
  searchEndLatest: string | null = null;

  constructor(
    private service: TournamentService,
    private notification: ToastrService,
  ) { }


  ngOnInit(): void {
    // Initialization logic, if needed
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
          this.tournaments = data;
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

  // Add component methods or event handlers as needed
  // ...

}
