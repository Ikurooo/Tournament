import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { formatIsoDate } from '../util/date-helper';
import { TournamentCreateDto, TournamentDetailDto, TournamentListDto } from '../dto/tournament';
import {HorseSearch} from "../dto/horse";

const baseUri = `${environment.backendUrl}/tournaments`;

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(private http: HttpClient) {}

  create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    // TODO: Implement the actual API call for creating a tournament
    return throwError(() => ({ message: 'Not implemented yet' }));
  }

  search(searchParams: HorseSearch): Observable<TournamentListDto[]> {
    let params = new HttpParams();

    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.bornEarliest) {
      params = params.append('bornEarliest', formatIsoDate(searchParams.bornEarliest));
    }
    if (searchParams.bornLastest) {
      params = params.append('bornLatest', formatIsoDate(searchParams.bornLastest));
    }

    return this.http.get<TournamentListDto[]>(baseUri, { params }).pipe(
      map(tournaments => tournaments.map(tournament => ({
        ...tournament,
        dateOfBirth: new Date(tournament.endDate) // Parse date string
      })))
    );
  }
}
