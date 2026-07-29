// Ces enums reflètent exactement les enums Java côté backend (be.ephec.padel.models.enums)
// pour garder la cohérence des valeurs échangées avec l'API

export enum TypeMembre {
  GLOBAL = 'GLOBAL',
  SITE = 'SITE',
  LIBRE = 'LIBRE'
}

export enum TypeMatch {
  PRIVE = 'PRIVE',
  PUBLIC = 'PUBLIC'
}

export enum StatutMatch {
  EN_ATTENTE = 'EN_ATTENTE',
  COMPLET = 'COMPLET',
  ANNULE = 'ANNULE',
  DEVENU_PUBLIC = 'DEVENU_PUBLIC'
}

export enum StatutPaiement {
  INSCRIT = 'INSCRIT',
  PAYE = 'PAYE',
  ANNULE = 'ANNULE'
}

export enum RoleAdmin {
  ADMIN_GLOBAL = 'ADMIN_GLOBAL',
  ADMIN_SITE = 'ADMIN_SITE'
}