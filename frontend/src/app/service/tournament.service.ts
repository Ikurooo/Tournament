import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, throwError } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { formatIsoDate } from '../util/date-helper';
import {
  TournamentCreateDto,
  TournamentDetailDto,
  TournamentListDto,
  TournamentSearchParams,
  TournamentStandingsDto
} from '../dto/tournament';

const baseUri = `${environment.backendUrl}/tournaments`;

@Injectable({
  providedIn: 'root'
})
export class TournamentService {
  constructor(private http: HttpClient) {}

  create(tournament: TournamentCreateDto): Observable<TournamentDetailDto> {
    return this.http.post<TournamentDetailDto>(baseUri, tournament);
  }

  getById(id: string) {
    return this.http.get<TournamentDetailDto>(`${baseUri}/${id}`);
  }

  search(searchParams: TournamentSearchParams): Observable<TournamentListDto[]> {
    let params = new HttpParams();

    if (searchParams.name) {
      params = params.append('name', searchParams.name);
    }
    if (searchParams.startDate) {
      params = params.append('startDate', formatIsoDate(searchParams.startDate));
    }
    if (searchParams.endDate) {
      params = params.append('endDate', formatIsoDate(searchParams.endDate));
    }

    return this.http.get<TournamentListDto[]>(baseUri, { params })
  }
}
