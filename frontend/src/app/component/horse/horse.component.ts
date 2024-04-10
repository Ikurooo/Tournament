import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseListDto} from '../../dto/horse';
import {HorseSearch} from '../../dto/horse';
import {debounceTime, map, Observable, of, Subject} from 'rxjs';
import {BreedService} from "../../service/breed.service";
import {DeletionResponseDto} from "../../dto/deletion-response";
import {HorseDeletedComponent} from "./horse-deleted/horse-deleted.component";
import {ErrorFormatterService} from "../../service/error-formatter.service";

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: HorseListDto[] = [];
  bannerError: string | null = null;
  searchParams: HorseSearch = {};
  searchBornEarliest: string | null = null;
  searchBornLatest: string | null = null;
  horseForDeletion: Horse | undefined;
  searchChangedObservable = new Subject<void>();

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private horseService: HorseService,
    private notification: ToastrService,
    private errorFormatter: ErrorFormatterService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.searchChangedObservable
      .pipe(debounceTime(300))
      .subscribe({next: () => this.reloadHorses()});
  }

  reloadHorses() {
    if (this.searchBornEarliest == null || this.searchBornEarliest === "") {
      delete this.searchParams.bornEarliest;
    } else {
      this.searchParams.bornEarliest = new Date(this.searchBornEarliest);
    }
    if (this.searchBornLatest == null || this.searchBornLatest === "") {
      delete this.searchParams.bornLastest;
    } else {
      this.searchParams.bornLastest = new Date(this.searchBornLatest);
    }
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }
  searchChanged(): void {
    this.searchChangedObservable.next();
  }

  setHorseForDeletion(id: string): void {
    this.horseService.getById(id).subscribe(
      {
        next: (horse: Horse) => {
          this.horseForDeletion = horse;

        },
        error: (err) => {
          this.notification.error(this.errorFormatter.format(err), "Failed to set horse for deletion.", {
            enableHtml: true,
            timeOut: 10000,
          });
        },
      }
    )
  }

  onDelete(horseId: string) {
    if (horseId) {
      this.horseService.deleteHorse(horseId).subscribe({
        next: (response: DeletionResponseDto) => {
          if (response.success) {
            this.notification.success("Horse deleted successfully.")
          }
          this.reloadHorses();
        },
        error: (err) => {
          this.notification.error(this.errorFormatter.format(err), "Failed to fetch horses.", {
            enableHtml: true,
            timeOut: 10000,
          });
        },
      });
      this.reloadHorses();
    }
  }

  breedSuggestions = (input: string): Observable<string[]> =>
    this.breedService.breedsByName(input, 5)
      .pipe(map(bs =>
        bs.map(b => b.name)));

  formatBreedName = (name: string) => name; // It is already the breed name, we just have to give a function to the component

}
