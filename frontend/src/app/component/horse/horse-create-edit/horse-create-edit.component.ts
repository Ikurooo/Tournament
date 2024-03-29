import { Component, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';
import { Horse } from 'src/app/dto/horse';
import { Sex } from 'src/app/dto/sex';
import { HorseService } from 'src/app/service/horse.service';
import { Breed } from "../../../dto/breed";
import { BreedService } from "../../../service/breed.service";
import { DeletionResponseDto } from "../../../dto/deletion-response";

export enum HorseCreateEditMode {
  create,
  edit,
}

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    sex: Sex.female,
    dateOfBirth: new Date(),
    height: 0,
    weight: 0,
  };

  private heightSet: boolean = false;
  private weightSet: boolean = false;
  private dateOfBirthSet: boolean = false;

  private horseId: string | null = null;

  get height(): number | null {
    return this.heightSet
      ? this.horse.height
      : null;
  }

  set height(value: number) {
    this.heightSet = true;
    this.horse.height = value;
  }

  get weight(): number | null {
    return this.weightSet
      ? this.horse.weight
      : null;
  }

  set weight(value: number) {
    this.weightSet = true;
    this.horse.weight = value;
  }

  get dateOfBirth(): Date | null {
    return this.dateOfBirthSet
      ? this.horse.dateOfBirth
      : null;
  }

  set dateOfBirth(value: Date) {
    this.dateOfBirthSet = true;
    this.horse.dateOfBirth = value;
  }

  constructor(
    private service: HorseService,
    private breedService: BreedService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit ' + this.horse?.name + "'s Details";
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Update';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }

  get sex(): string {
    switch (this.horse.sex) {
      case Sex.male: return 'Male';
      case Sex.female: return 'Female';
      default: return '';
    }
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if (this.mode === HorseCreateEditMode.edit) {
        // Load horse details for editing
        this.route.paramMap.subscribe(params => {
          this.horseId = params.get('id');
          if (this.horseId) {
            this.service.getById(this.horseId).subscribe({
              next: data => {
                this.horse = data;
              },
              error: error => {
                console.error('Error fetching horse details', error);
              }
            });
          }
        });
      }
    });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatBreedName(breed: Breed | null): string {
    return breed?.name ?? '';
  }

  breedSuggestions = (input: string) => (input === '')
    ? of([])
    : this.breedService.breedsByName(input, 5);

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          observable = this.service.update(this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: err => {
          let errorMessage = 'An error occurred';
          if (err.error instanceof ErrorEvent) {
            // Client-side error
            errorMessage = `Client-side error: ${err.error.message}`;
          } else if (err.error && typeof err.error === 'object') {
            // Server-side error
            const errorObject = err.error;
            if (errorObject.errors && Array.isArray(errorObject.errors)) {
              errorMessage = errorObject.errors.join(', ');
            } else if (errorObject.message) {
              errorMessage = errorObject.message;
            }
          } else {
            // Other types of errors
            errorMessage = `Server-side error: ${err.statusText}`;
          }
          this.notification.error("Failure", errorMessage);
        }
      });
    }
  }

  onDelete() {
    console.log('onDelete called');
    if (this.horseId) {
      this.service.deleteHorse(this.horseId).subscribe({
        next: (response: DeletionResponseDto) => {
          if (response.success) {
            console.log('Horse deleted successfully');
            this.router.navigate(['horses','deletion-successful']);
          } else {
            console.error('Error deleting horse:', response.message);
            // Handle the error message or navigate as needed
            this.router.navigate(['horses', 'deletion-failed']);
          }
        },
        error: (error) => {
          console.error('Unexpected error:', error);
          // Handle other errors if needed
          this.router.navigate(['horses', 'deletion-failed']);
        }
      });
    }
  }
}
